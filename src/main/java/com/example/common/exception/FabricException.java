package com.example.common.exception;

import lombok.Data;

@Data
public class FabricException extends RuntimeException{
    private String errMessage;

    public FabricException() {
    }

    public FabricException(String message) {
        super(message);
        this.errMessage=message;
    }

    public static void cast(String message){
        throw new FabricException(message);
    }

    public static void cast(CommonError commonError){
        throw new FabricException(commonError.getErrMessage());
    }
}
