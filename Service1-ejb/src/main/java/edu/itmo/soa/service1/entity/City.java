package edu.itmo.soa.service1.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Entity
@Table(name = "cities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class City implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Positive
    private int id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "coordinates_id", nullable = false)
    private Coordinates coordinates;


    @NotNull
    @Column(nullable = false, updatable = false)
    @Builder.Default
    private ZonedDateTime creationDate = ZonedDateTime.now();

    @NotNull
    @Positive
    @Column(nullable = false)
    private Integer area;

    @PositiveOrZero
    @Column(nullable = false)
    private long population;

    private Integer metersAboveSeaLevel;

    private LocalDateTime establishmentDate;

    @Positive
    private Float populationDensity;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Government government;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "governor_id", nullable = false)
    private Human governor;
}
