package org.example.dto;

import jakarta.validation.constraints.Max;
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
public class StudentDTO {

    private int id;

    @Size(max = 50, min = 2)
    private String name;

    @Min(value = 16)
    @Max(value = 62)
    private int age;

    private RecordBookDTO recordBookDTO;

    private GrooupDTO grooupDTO;

}
