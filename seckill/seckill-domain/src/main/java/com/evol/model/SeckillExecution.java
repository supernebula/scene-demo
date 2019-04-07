package com.evol.model;

import com.evol.entity.SeckillOrder;
import lombok.Data;

@Data
public class SeckillExecution {

    private Long seckillId;

    //秒杀执行结果状态
    private int state;

    //状态表示
    private String stateInfo;

    //秒杀成功的订单对象
    private SeckillOrder seckillOrder;

    public SeckillExecution(Long seckillId, int state, String stateInfo, SeckillOrder seckillOrder) {
        this.seckillId = seckillId;
        this.state = state;
        this.stateInfo = stateInfo;
        this.seckillOrder = seckillOrder;
    }

    public SeckillExecution(Long seckillId, int state, String stateInfo) {
        this.seckillId = seckillId;
        this.state = state;
        this.stateInfo = stateInfo;
    }
}