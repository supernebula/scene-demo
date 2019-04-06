package com.evol.service;

import com.evol.entity.Seckill;
import com.evol.exception.RepeatKillException;
import com.evol.exception.SeckillCloseException;
import com.evol.exception.SeckillException;
import com.evol.model.Exposer;
import com.evol.model.SeckillExecution;

import java.util.List;

public interface SeckillService {

    /**
     * 获取所有的秒杀商品列表
     *
     * @return
     */
    List<Seckill> findAll();

    /**
     * 获取某一条商品秒杀信息
     *
     * @param seckillId
     * @return
     */
    Seckill findById(long seckillId);

    /**
     * 秒杀开始时输出暴露秒杀的地址
     * 否者输出系统时间和秒杀时间
     *
     * @param seckillId
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀的操作
     *
     * @param seckillId
     * @param userPhone
     * @param payAmount
     * @param md5
     */
    SeckillExecution executeSeckill(long seckillId, long payAmount, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException;
}
