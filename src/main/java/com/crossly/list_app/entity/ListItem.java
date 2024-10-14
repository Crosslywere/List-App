package com.crossly.list_app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListItem {

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private String title;

	private String description;

	@Column(nullable = false)
	private LocalDateTime timeCreated;

	@Builder.Default
	private Boolean completed = false;

}
