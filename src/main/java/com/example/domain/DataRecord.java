/*
 * SPDX-License-Identifier: Apache-2.0
 */

package com.example.domain;


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
public class DataRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private String uuid;

    private String dataID;

    private String fileName;

    private String type;

    private String filePath;

    private String dataDescription;

    private String owner;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;



}



