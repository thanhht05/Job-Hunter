package vn.JobHunter.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultPaginationDto {
    private Meta meta;
    private Object result;
}
