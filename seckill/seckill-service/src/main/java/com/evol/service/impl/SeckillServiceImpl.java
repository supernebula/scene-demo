package com.evol.service.impl;

import com.evol.entity.Seckill;
import com.evol.exception.RepeatKillException;
import com.evol.exception.SeckillCloseException;
import com.evol.exception.SeckillException;
import com.evol.model.Exposer;
import com.evol.model.SeckillExecution;
import com.evol.service.SeckillService;
import com.evol.dao.mapper

import java.util.List;


public class SeckillServiceImpl implements SeckillService {

    //设置盐值字符串，随便定义，用于混淆MD5值
    private final String salt = "sjajaspu-i-2jrfm;sd";
    @Autowired
    private SeckillMapper seckillMapper;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Override
    public List<Seckill> findAll() {
        return null;
    }

    @Override
    public Seckill findById(long seckillId) {
        return null;
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = seckillMapper
    }

    @Override
    public SeckillExecution executeSeckill(long seckillId, long payAmount, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        return null;
    }
}
