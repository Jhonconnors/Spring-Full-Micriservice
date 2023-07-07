package com.example.model;
import lombok.*;

import javax.persistence.*;

import io.swagger.annotations.ApiModelProperty;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "employees")
public class Employee {

	@ApiModelProperty(position = 0)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
	
	@ApiModelProperty(position = 1)
    @Column(name = "first_name", nullable = false)
    private String firstName;

	@ApiModelProperty(position = 2)
    @Column(name = "last_name", nullable = false)
    private String lastName;

	@ApiModelProperty(position = 3)
    @Column(nullable = false)
    private String email;
}