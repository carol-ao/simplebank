package com.carol.simplebank.exceptions;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class StandardError {

  private LocalDateTime timestamp;
  private Integer status;
  private String message;
  private String path;
}
