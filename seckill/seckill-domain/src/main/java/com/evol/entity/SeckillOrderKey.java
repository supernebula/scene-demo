package com.evol.entity;

public class SeckillOrderKey {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column seckill_order.seckill_id
     *
     * @mbg.generated
     */
    private Long seckillId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column seckill_order.user_phone
     *
     * @mbg.generated
     */
    private Long userPhone;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column seckill_order.seckill_id
     *
     * @return the value of seckill_order.seckill_id
     *
     * @mbg.generated
     */
    public Long getSeckillId() {
        return seckillId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column seckill_order.seckill_id
     *
     * @param seckillId the value for seckill_order.seckill_id
     *
     * @mbg.generated
     */
    public void setSeckillId(Long seckillId) {
        this.seckillId = seckillId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column seckill_order.user_phone
     *
     * @return the value of seckill_order.user_phone
     *
     * @mbg.generated
     */
    public Long getUserPhone() {
        return userPhone;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column seckill_order.user_phone
     *
     * @param userPhone the value for seckill_order.user_phone
     *
     * @mbg.generated
     */
    public void setUserPhone(Long userPhone) {
        this.userPhone = userPhone;
    }
}