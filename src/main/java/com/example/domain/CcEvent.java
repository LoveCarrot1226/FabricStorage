package com.example.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CcEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    //区块号
    private Long blockNumber;

    //交易单号
    private String transactionId;

    //链码名称
    private String chaincodeName;

    //时间名
    private String eventName;

    //地址id
    private String payload;

    //创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    //更新时间
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;


    public CcEvent(long blockNumber, String transactionId, String chaincodeName, String eventName, String payload) {
        this.blockNumber=blockNumber;
        this.transactionId=transactionId;
        this.chaincodeName=chaincodeName;
        this.eventName=eventName;
        this.payload=payload;
    }
}
