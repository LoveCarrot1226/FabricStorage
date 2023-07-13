/*
 * SPDX-License-Identifier: Apache-2.0
 */

package com.example.domain;


import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DataRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String uuid;

    private String dataID;

    private String applicant;

    private String usage;

    private String hostIP;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;



}



