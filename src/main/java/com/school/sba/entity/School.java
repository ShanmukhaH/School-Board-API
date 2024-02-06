package com.school.sba.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class School {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int schoolId;
	private String schoolName;
	private long contanctNo;
	private String emaild;
	private String adress;
	private boolean isDeleted;
	
	@OneToOne(cascade =CascadeType.ALL)
	private Schedule schedule;
	
	@OneToMany(mappedBy = "school")
	private List<AcademicProgram>  programlist=new ArrayList<AcademicProgram>();
	
	
}
