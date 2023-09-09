package com.unasp.taskmanagement.config.messages;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Messages {
    private String message;
    private Integer code;

    public Messages converter(String message, Integer code) {
      return Messages.builder()
          .message(message)
          .code(code)
          .build();
    }
}