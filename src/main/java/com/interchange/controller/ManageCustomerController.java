package com.interchange.controller;

import com.interchange.dto.ManageCustomerAndStaffDTO.AddCustomerAndStaffDTO;
import com.interchange.dto.ManageCustomerAndStaffDTO.UpdateCustomerAndStaffDTO;
import com.interchange.repository.UserRepository;
import com.interchange.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@CrossOrigin("*")
@RequestMapping("/api/manageCustomer")
public class ManageCustomerController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomerService customerService;
    @GetMapping("/showCustomerList")
    public ResponseEntity<?> getCustomers() {
        return customerService.getCustomers();
    }
    @GetMapping("/showCustomer/{userId}")
    public ResponseEntity<?> getCustomer(@PathVariable("userId") String userId) {
        return customerService.getCustomerById(userId);
    }
    @PostMapping("/addCustomer")
    public  ResponseEntity<?> addCustomer(@Valid @RequestBody AddCustomerAndStaffDTO addCustomerAndStaffDTO) {
        if (Boolean.TRUE.equals(userRepository.existsByUserId(addCustomerAndStaffDTO.getUserId()))) {
            return new ResponseEntity<>("User ID had in the system", HttpStatus.CONFLICT);
        }
        if (Boolean.TRUE.equals(userRepository.existsByEmail(addCustomerAndStaffDTO.getEmail()))) {
            return new ResponseEntity<>("Email had in the system", HttpStatus.CONFLICT);
        }
        if (Boolean.TRUE.equals(userRepository.existsByPhoneNumber(addCustomerAndStaffDTO.getPhoneNumber()))) {
            return new ResponseEntity<>("Phone number had in the system", HttpStatus.CONFLICT);
        }
        if(!addCustomerAndStaffDTO.isOver18()) {
            return new ResponseEntity<>("You must more than 18 years old", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(customerService.addCustomer(addCustomerAndStaffDTO));
    }
    @PutMapping("/updateCustomer/{userId}")
    public  ResponseEntity<?> updateCustomer(@PathVariable("userId") String userId,
                                             @Valid @RequestBody UpdateCustomerAndStaffDTO updateCustomerAndStaffDTO) {
        if(!updateCustomerAndStaffDTO.isOver18()) {
            return new ResponseEntity<>("You must more than 18 years old", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(customerService.updateCustomer(userId, updateCustomerAndStaffDTO));
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
