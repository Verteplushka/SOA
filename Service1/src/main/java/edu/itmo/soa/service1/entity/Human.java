package edu.itmo.soa.service1.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "humans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Human implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private int age;

    public Human(int  age) {
        this.age = age;
    }
}
