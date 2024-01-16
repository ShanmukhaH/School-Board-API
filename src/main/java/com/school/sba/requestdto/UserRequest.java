package com.school.sba.requestdto;

import com.school.sba.enums.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {


	@NotEmpty(message = "username cannot be null")
	@Pattern(regexp = "^[A-Z][a-zA-Z]*( [A-Z][a-zA-Z]*)?$", 
    message = "Username should start with an uppercase letter, and if there are two names, the second name should start with an uppercase letter.")
	private String userName;
	@Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message = "Password must"
			+ " contain at least one letter, one number, one special character")
	private String password;
	private String firstName;
	private String lastName;
	private long contactNo;
	@NotEmpty(message = "email is cannot be blank")
	@Email(regexp = "[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,}", message = "invalid email ")
	private String email;
	private UserRole userRole;
}
