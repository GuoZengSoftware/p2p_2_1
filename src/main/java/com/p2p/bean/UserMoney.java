package com.p2p.bean;

import java.math.BigDecimal;

/**
 * Created by 7025 on 2017/12/19.
 * 对应用户资金
 */
public class UserMoney {
    private Integer umid;

    private Integer uid;

    private BigDecimal zmoney;

    private BigDecimal kymoney;

    private BigDecimal symoney;

    private BigDecimal tzmoney;

    private BigDecimal djmoney;

    private BigDecimal dsmoney;

    private BigDecimal jlmoney;

    public UserMoney(Integer umid, Integer uid, BigDecimal zmoney, BigDecimal kymoney, BigDecimal symoney, BigDecimal tzmoney, BigDecimal djmoney, BigDecimal dsmoney, BigDecimal jlmoney) {
        this.umid = umid;
        this.uid = uid;
        this.zmoney = zmoney;
        this.kymoney = kymoney;
        this.symoney = symoney;
        this.tzmoney = tzmoney;
        this.djmoney = djmoney;
        this.dsmoney = dsmoney;
        this.jlmoney = jlmoney;
    }

    public UserMoney() {
        super();
    }

    public Integer getUmid() {
        return umid;
    }

    public void setUmid(Integer umid) {
        this.umid = umid;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public BigDecimal getZmoney() {
        return zmoney;
    }

    public void setZmoney(BigDecimal zmoney) {
        this.zmoney = zmoney;
    }

    public BigDecimal getKymoney() {
        return kymoney;
    }

    public void setKymoney(BigDecimal kymoney) {
        this.kymoney = kymoney;
    }

    public BigDecimal getSymoney() {
        return symoney;
    }

    public void setSymoney(BigDecimal symoney) {
        this.symoney = symoney;
    }

    public BigDecimal getTzmoney() {
        return tzmoney;
    }

    public void setTzmoney(BigDecimal tzmoney) {
        this.tzmoney = tzmoney;
    }

    public BigDecimal getDjmoney() {
        return djmoney;
    }

    public void setDjmoney(BigDecimal djmoney) {
        this.djmoney = djmoney;
    }

    public BigDecimal getDsmoney() {
        return dsmoney;
    }

    public void setDsmoney(BigDecimal dsmoney) {
        this.dsmoney = dsmoney;
    }

    public BigDecimal getJlmoney() {
        return jlmoney;
    }

    public void setJlmoney(BigDecimal jlmoney) {
        this.jlmoney = jlmoney;
    }
}