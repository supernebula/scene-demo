package com.evol.dao.mapper.custom;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

public interface CustomSeckillMapper {

    int reduceStock(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    int insertOrder(@Param("seckillId") long seckillId, @Param("payAmount") long payAmount, @Param("userPhone") long userPhone);
}
