package ru.practicum.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "locations")
public class Location {
    private Float lat;
    private Float lon;
}
