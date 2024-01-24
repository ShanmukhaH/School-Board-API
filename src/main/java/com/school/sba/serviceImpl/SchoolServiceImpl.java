package com.school.sba.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.School;
import com.school.sba.entity.User;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.AdminiNotFoundByUserRoleException;
import com.school.sba.exception.DuplicateEntryException;
import com.school.sba.exception.SchoolAlreadyPresentException;
import com.school.sba.exception.UserNotFoundByIdException;
import com.school.sba.repoistory.SchoolRepoistory;
import com.school.sba.repoistory.UserRepoistory;
import com.school.sba.requestdto.SchoolRequest;
import com.school.sba.responsedto.SchoolResponse;
import com.school.sba.service.SchoolService;
import com.school.sba.utlity.ResponseStructure;

@Service
public class SchoolServiceImpl implements SchoolService {

	@Autowired
	private SchoolRepoistory schoolRepoistory;
	
	@Autowired
	private UserRepoistory userRepoistory;
	
	@Autowired
	private ResponseStructure<SchoolResponse> structure;

	public School mapToSchool(SchoolRequest schoolRequest) {
		return School.builder()
				.schoolName(schoolRequest.getSchoolName())
				.contanctNo(schoolRequest.getContanctNo())
				.emaild(schoolRequest.getEmaild())
				.adress(schoolRequest.getAdress())
				.build();
	}
	
	public SchoolResponse mapToSchoolResponses(School school) {
		return SchoolResponse.builder()
				.schoolId(school.getSchoolId())
				.schoolName(school.getSchoolName())
				.contanctNo(school.getContanctNo())
				.emaild(school.getEmaild())
				.adress(school.getAdress())
				.build();
	}
	
	@Override
	public ResponseEntity<ResponseStructure<SchoolResponse>> saveSchool(SchoolRequest request,int userId) {
		
		
		return userRepoistory.findById(userId).map(u->{
			if(u.getUserRole().equals(UserRole.ADMIN)) {
				if(u.getSchool()==null) {
					School school = mapToSchool(request);
					school=schoolRepoistory.save(school);
					u.setSchool(school);
					userRepoistory.save(u);
					structure.setStatus(HttpStatus.CREATED.value());
					structure.setMessage("School Saved Sucessfully!!!");
					structure.setData(mapToSchoolResponses(school));
					return new  ResponseEntity<ResponseStructure<SchoolResponse>>(structure,HttpStatus.CREATED);
				}
				else
				throw new SchoolAlreadyPresentException("School is alredy Present");
			}else
				throw new AdminiNotFoundByUserRoleException("Admini not Found");
			
		}).orElseThrow(()->new UserNotFoundByIdException("Failed to save School"));
				
		}
		

}
