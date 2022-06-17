package com.springboot.blog.service.impl;

import com.springboot.blog.entity.Post;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ModelMapper mapper;

    private Page<Post> posts;

//    public PostServiceImpl(PostRepository postRepository, ModelMapper mapper) {
//        this.postRepository = postRepository;
//        this.mapper = mapper;
//    }

    @Override
    public PostDto createPost(PostDto postDto) {

        // Convert PostDto to Post
        Post post = mapToEntity(postDto);

        // save
        Post newPost = postRepository.save(post);

        // Convert Post to PostDto
        PostDto postResponse = mapToDTO(newPost);

        return postResponse;
    }

    @Override
    public PostResponse getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        // create pageable content
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Post> posts = postRepository.findAll(pageable);

        // get content for page object
        List<Post> listOfPosts = posts.getContent();
        List<PostDto> content =  listOfPosts.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());

        PostResponse postResponse = new PostResponse();
        postResponse.setContent(content);
        postResponse.setPageNo(posts.getNumber());
        postResponse.setPageSize(posts.getSize());
        postResponse.setTotalElements(posts.getTotalElements());
        postResponse.setTotalPages(posts.getTotalPages());
        postResponse.setLast(posts.isLast());

        return postResponse;
    }

    @Override
    public PostDto getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        return mapToDTO(post);
    }

    @Override
    public PostDto updatePost(PostDto postDto, Long id) {
        // find Post by Id from database
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setContent(postDto.getContent());
        // save
        Post postUpdate = postRepository.save(post);

        // convert Entity to Dto
        return mapToDTO(postUpdate);
    }

    @Override
    public void deletePostById(long id) {
        // find post by id from database
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        // delete
        postRepository.delete(post);
    }

    /**
     * Convert Post to PostDto
     * @param post
     * @return postDto
     */
    private PostDto mapToDTO(Post post) {
//        PostDto postDto = new PostDto();
//        postDto.setId(post.getId());
//        postDto.setTitle((post.getTitle()));
//        postDto.setDescription(post.getDescription());
//        postDto.setContent(post.getContent());
        PostDto postDto = mapper.map(post, PostDto.class);
        return postDto;
    }

    /**
     * Convert PostDto to Post
     * @param postDto
     * @return post
     */
    private Post mapToEntity(PostDto postDto) {
//        Post post = new Post();
//        post.setTitle((postDto.getTitle()));
//        post.setDescription(postDto.getDescription());
//        post.setContent(postDto.getContent());
        Post post = mapper.map(postDto, Post.class);
        return post;
    }
}
