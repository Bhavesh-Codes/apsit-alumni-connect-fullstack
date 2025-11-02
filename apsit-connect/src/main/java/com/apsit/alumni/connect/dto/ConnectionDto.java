package com.apsit.alumni.connect.dto;

import com.apsit.alumni.connect.model.ConnectionStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ConnectionDto {

    private Long id;
    private ConnectionStatus status;
    private LocalDateTime createdAt;

    // Instead of the full User, we show a 'safe' summary
    private UserSummaryDto fromUser; 

    // We don't need 'toUser' since the person
    // fetching this list *is* the 'toUser'
}