package org.example.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GrooupDTO {

    private int id;

    @Size(max = 20, min = 2)
    private String tittle;

    @Min(value = 1)
    private int room;
}
