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
import org.springframework.data.redis.core.RedisTemplate;
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

    //设置秒杀redis缓存的key
    private final String REDIS_SECKILL_KEY = "seckill";

    @Autowired
    private RedisTemplate redisTemplate;

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

//    /**
//     * 从数据库查询所有秒杀商品
//     * @return
//     */
//    @Override
//    public List<Seckill> findAll() {
//        SeckillExample example = new SeckillExample();
//        List<Seckill> list = seckillMapper.selectByExample(example);
//        return list;
//    }

    /**
     * 从Redis查询所有秒杀商品
     * @return
     */
    @Override
    public List<Seckill> findAll() {
        List<Seckill> seckillList = redisTemplate.boundHashOps("seckill").values();
        if (seckillList == null || seckillList.size() == 0){
            //说明缓存中没有秒杀列表数据
            //查询数据库中秒杀列表数据，并将列表数据循环放入redis缓存中
            //seckillList = seckillMapper.findAll();
            SeckillExample example = new SeckillExample();
            seckillList = seckillMapper.selectByExample(example);
            for (Seckill seckill : seckillList){
                //将秒杀列表数据依次放入redis缓存中，key:秒杀表的ID值；value:秒杀商品数据
                redisTemplate.boundHashOps(REDIS_SECKILL_KEY).put(seckill.getId(), seckill);
                logger.info("findAll -> 从数据库中读取放入缓存中");
            }
        }else{
            logger.info("findAll -> 从缓存中读取");
        }
        return seckillList;
    }

    @Override
    public Seckill findById(long seckillId) {
        Seckill seckill = seckillMapper.selectByPrimaryKey(seckillId);
        return seckill;
    }

//    /**
//     * 根据数据库中生成Url
//     * @param seckillId
//     * @return
//     */
//    @Override
//    public Exposer exportSeckillUrl(long seckillId) {
//        Seckill seckill = seckillMapper.selectByPrimaryKey(seckillId);
//        if(seckill == null){
//            //没有查到
//            return new Exposer(false, seckillId);
//        }
//        Date startTime = seckill.getStartTime();
//        Date endTime = seckill.getEndTime();
//        Date nowTime = new Date();
//        if(nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()){
//            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
//        }
//        String md5 = getMD5(seckillId);
//        return new Exposer(true, md5, seckillId);
//    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = (Seckill) redisTemplate.boundHashOps(REDIS_SECKILL_KEY).get(seckillId);
        if (seckill == null) {
            //说明redis缓存中没有此key对应的value
            //查询数据库，并将数据放入缓存中
            seckill = seckillMapper.selectByPrimaryKey(seckillId);
            if (seckill == null) {
                //说明没有查询到
                return new Exposer(false, seckillId);
            } else {
                //查询到了，存入redis缓存中。 key:秒杀表的ID值； value:秒杀表数据
                redisTemplate.boundHashOps(REDIS_SECKILL_KEY).put(seckill.getId(), seckill);
                logger.info("RedisTemplate -> 从数据库中读取并放入缓存中");
            }
        } else {
            logger.info("RedisTemplate -> 从缓存中读取");
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        //获取系统时间
        Date nowTime = new Date();
        if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        //转换特定字符串的过程，不可逆的算法
        String md5 = getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    /**
     * 数据库秒杀
     * @param seckillId
     * @param payAmount
     * @param userPhone
     * @param md5
     * @return
     * @throws SeckillException
     * @throws RepeatKillException
     * @throws SeckillCloseException
     */
    //@Override
    @Transactional
    public SeckillExecution executeSeckillByData(long seckillId, long payAmount, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        if(md5 == null || !md5.equals(getMD5(seckillId))){
            throw new SeckillException("seckill data rewrite");
        }

        //执行秒杀逻辑：1.减库存， 2.存储秒杀订单
        Date nowTime = new Date();

        try {
            //记录秒杀订单信息
            Integer insertCount = customSeckillMapper.insertOrder(seckillId, payAmount, userPhone);
            //唯一值：seckillId、userPhone，保证一个用户只能秒杀意见商品
            if (insertCount == null || insertCount <= 0) {
                //重复秒杀
                throw new RepeatKillException("seckill repeated");
            } else {
                //减库存
                Integer updateCount = customSeckillMapper.reduceStock(seckillId, nowTime);
                if (updateCount == null || updateCount <= 0) {
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

    /**
     * 数据库+Redis秒杀
     * @param seckillId
     * @param payAmount
     * @param userPhone
     * @param md5
     * @return
     * @throws SeckillException
     * @throws RepeatKillException
     * @throws SeckillCloseException
     */
    @Override
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long payAmount, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException("seckill data rewrite");
        }
        //执行秒杀逻辑：1.减库存；2.储存秒杀订单
        Date nowTime = new Date();

        try {
            //记录秒杀订单信息
            Integer insertCount = customSeckillMapper.insertOrder(seckillId, payAmount, userPhone);
            //唯一性：seckillId,userPhone，保证一个用户只能秒杀一件商品
            if (insertCount == null || insertCount <= 0) {
                //重复秒杀
                throw new RepeatKillException("seckill repeated");
            } else {
                //减库存
                Integer updateCount = customSeckillMapper.reduceStock(seckillId, nowTime);
                if (updateCount <= 0) {
                    //没有更新记录，秒杀结束
                    throw new SeckillCloseException("seckill is closed");
                } else {
                    //秒杀成功
                    SeckillOrderKey key = new SeckillOrderKey();
                    key.setSeckillId(seckillId);
                    key.setUserPhone(userPhone);
                    SeckillOrder seckillOrder = seckillOrderMapper.selectByPrimaryKey(key);

                    //更新缓存（更新库存数量）
                    Seckill seckill = (Seckill) redisTemplate.boundHashOps(REDIS_SECKILL_KEY).get(seckillId);
                    seckill.setStockNumber(seckill.getStockNumber() - 1);
                    redisTemplate.boundHashOps(key).put(seckillId, seckill);

                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS.getState(), SeckillStatEnum.SUCCESS.getStateInfo(), seckillOrder);
                }
            }
        } catch (SeckillCloseException e) {
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
