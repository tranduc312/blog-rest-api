package com.springboot.blog.payload;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@ApiModel(description = "Comment model information")
@Data
public class CommentDto {

    @ApiModelProperty(value = "Comment id")
    private Long id;

    @ApiModelProperty(value = "Comment name")
    @NotEmpty(message = "Name should not be null of empty")
    private String name;

    @ApiModelProperty(value = "Comment email")
    @NotEmpty(message = "Email should not be null or empty")
    @Email
    private String email;

    @ApiModelProperty(value = "Comment body")
    @NotEmpty
    @Size(min = 8, message = "Comment body must be minimum 10 characters")
    private String body;
}
