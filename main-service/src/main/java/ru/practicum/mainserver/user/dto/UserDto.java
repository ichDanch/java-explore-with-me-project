package ru.practicum.mainserver.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.mainserver.validation.CreateValidation;
import ru.practicum.mainserver.validation.UpdateValidation;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor()
@NoArgsConstructor
@Builder
public class UserDto {
    @NotNull(groups = UpdateValidation.class)
    private long id;
    @NotNull(groups = {CreateValidation.class, UpdateValidation.class})
    @NotBlank(groups = {CreateValidation.class, UpdateValidation.class}, message = "Name must not be blank")
    private String name;
    @Email(message = "Email should be valid")
    @NotNull(groups = {CreateValidation.class, UpdateValidation.class})
    private String email;
}
