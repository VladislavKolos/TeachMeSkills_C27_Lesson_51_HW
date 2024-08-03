package org.example.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "record_book")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecordBook {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "rating")
    @Min(value = 10)
    @Max(value = 100)
    private int rating;

    @OneToOne(mappedBy = "recordBook")
    private Student student;
}
