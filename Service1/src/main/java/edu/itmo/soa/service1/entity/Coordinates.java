package edu.itmo.soa.service1.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "coordinates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coordinates {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Double x;

    @NotNull
    private Double y;

    public Coordinates(Double x, Double y) {
        this.x = x;
        this.y = y;
    }
}

