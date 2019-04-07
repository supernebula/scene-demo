package com.evol.service.impl;

import com.evol.dao.mapper.SeckillMapper;
import com.evol.dao.mapper.SeckillOrderMapper;
import com.evol.dao.mapper.custom.CustomSeckillMapper;
import com.evol.entity.Seckill;
import com.evol.entity.SeckillExample;
import com.evol.entity.SeckillOrder;
import com.evol.entity.SeckillOrderKey;
import com.evol.exception.RepeatKillException;
import com.evol.exception.SeckillCloseException;
import com.evol.exception.SeckillException;
import com.evol.model.Exposer;
import com.evol.model.SeckillExecution;
import com.evol.model.SeckillStatEnum;
import com.evol.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

@Service
public class SeckillServiceImpl implements SeckillService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //设置盐值字符串，随便定义，用于混淆MD5值
    private final String salt = "sjajaspu-i-2jrfm;sd";

    private String getMD5(Long seckillId){
        String base = seckillId + "/" + salt;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Autowired
    private SeckillMapper seckillMapper;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private CustomSeckillMapper customSeckillMapper;

    @Override
    public List<Seckill> findAll() {
        SeckillExample example = new SeckillExample();
        List<Seckill> list = seckillMapper.selectByExample(example);
        return list;
    }

    @Override
    public Seckill findById(long seckillId) {
        Seckill seckill = seckillMapper.selectByPrimaryKey(seckillId);
        return seckill;
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = seckillMapper.selectByPrimaryKey(seckillId);
        if(seckill == null){
            //没有查到
            return new Exposer(false, seckillId);
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        if(nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()){
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        String md5 = getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    @Override
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long payAmount, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        if(md5 == null || !md5.equals(getMD5(seckillId))){
            throw new SeckillException("seckill data rewrite");
        }

        //执行秒杀逻辑：1.减库存， 2.存储秒杀订单
        Date nowTime = new Date();

        try {
            //记录秒杀订单信息
            int insertCount = customSeckillMapper.insertOrder(seckillId, payAmount, userPhone);
            //唯一值：seckillId、userPhone，保证一个用户只能秒杀意见商品
            if (insertCount <= 0) {
                //重复秒杀
                throw new RepeatKillException("seckill repeated");
            } else {
                //减库存
                int updateCount = customSeckillMapper.reduceStock(seckillId, nowTime);
                if (updateCount <= 0) {
                    //没有更新记录，秒杀结束
                    throw new SeckillCloseException("seckill is closed");
                } else {
                    //秒杀成功
                    SeckillOrderKey key = new SeckillOrderKey();
                    key.setSeckillId(seckillId);
                    key.setUserPhone(userPhone);
                    SeckillOrder seckillOrder = seckillOrderMapper.selectByPrimaryKey(key);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS.getState(), SeckillStatEnum.SUCCESS.getStateInfo(), seckillOrder);
                }
            }
        }catch (SeckillCloseException e) {
            throw e;
        } catch (RepeatKillException e) {
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            //所有编译期异常，转换为运行期异常
            throw new SeckillException("seckill inner error:" + e.getMessage());
        }
    }
}
