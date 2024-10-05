package ar.edu.utn.frc.tup.lciii.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "countries")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountryEntity {

    @Id
    private String code;

    private String name;

}