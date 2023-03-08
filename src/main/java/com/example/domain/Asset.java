package com.example.domain;


import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Asset {

    private String assetID;

    private String color;

    private int size;

    private String owner;

    private int appraisedValue;


}
