package com.springboot.blog.controller;

import static com.springboot.blog.utils.AppConstants.DEFAULT_PAGE_NUMBER;
import static com.springboot.blog.utils.AppConstants.DEFAULT_PAGE_SIZE;
import static com.springboot.blog.utils.AppConstants.DEFAULT_SORT_BY;
import static com.springboot.blog.utils.AppConstants.DEFAULT_SORT_DIRECTION;

import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.service.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "CRUD Rest APIs for Post resources")
@RestController
public class PostController {

    private PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     *
     */
    @ApiOperation(value = "Create Post REST API")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/v1/posts")
    public ResponseEntity<PostDto> createPost(@Valid @RequestBody PostDto postDto) {
        return new ResponseEntity<>(postService.createPost(postDto), HttpStatus.CREATED);
    }

    /**
     *
     */
    @ApiOperation(value = "Get ALL Post REST API")
    @GetMapping("/api/v1/posts")
    public PostResponse getAllPosts(
            @RequestParam(value = "pageNo", defaultValue = DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ) {
        return postService.getAllPosts(pageNo, pageSize, sortBy, sortDir);
    }

    /**
     *
     */
    @ApiOperation(value = "Get Post By Id REST API")
    @GetMapping(value = "/api/v1/posts/{id}")
    public ResponseEntity<PostDto> getPostByIdV1(@PathVariable(name = "id") long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    /*@GetMapping(value = "/api/posts/{id}", produces = "application/vnd.javaguides.v2+json")
    public ResponseEntity<PostDtoV2> getPostByIdV2(@PathVariable(name = "id") long id) {
        PostDto  postDto = postService.getPostById(id);
        PostDtoV2 postDtoV2 = new PostDtoV2();
        postDtoV2.setId(postDto.getId());
        postDtoV2.setTitle(postDto.getTitle());
        postDtoV2.setDescription(postDto.getDescription());
        postDtoV2.setContent(postDto.getContent());

        List<String> tags = new ArrayList<>();
        tags.add("Java");
        tags.add("Spring Boot");
        tags.add("AWS");
        postDtoV2.setTags(tags);

        return ResponseEntity.ok(postDtoV2);
    }*/

    /**
     *
     */
    @ApiOperation(value = "Update Post By Id REST API")
    @PutMapping("/api/v1/posts/{id}")
    public ResponseEntity<PostDto> updatePost(@Valid @RequestBody PostDto postDto, @PathVariable(name = "id") long id) {
        PostDto postResponse = postService.updatePost(postDto, id);
        return new ResponseEntity<>(postResponse, HttpStatus.OK);
    }

    /**
     *
     */
    @ApiOperation(value = "Delete Post By Id REST API")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/api/v1/posts/{id}")
    public ResponseEntity<String> deletePost(@PathVariable(name = "id") long id) {
        postService.deletePostById(id);

        return new ResponseEntity<>("Post entity deleted successfully!", HttpStatus.OK);
    }
}
