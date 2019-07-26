package com.tw.api.unit.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.xml.internal.bind.v2.TODO;
import com.tw.api.unit.test.domain.todo.Todo;
import com.tw.api.unit.test.domain.todo.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TodoController.class)
@ActiveProfiles(profiles = "test")
class TodoControllerTest {
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoRepository todoRepository;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void should_return_todos_as_string_when_get_all_todo_succeed() throws Exception {
        List<Todo> todos = buildTodos();
        String expectedResultAsString = objectMapper.writeValueAsString(todos);
        when(todoRepository.getAll()).thenReturn(todos);

        ResultActions result = mockMvc.perform(get("/todos"));

        result.andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().json(expectedResultAsString));
    }

    @Test
    void should_return_target_todo_when_select_todo_succeed() throws Exception {
        Todo targetTodo = new Todo("title", true);
        when(todoRepository.findById(1)).thenReturn(Optional.of(targetTodo));

        ResultActions result = mockMvc.perform(get("/todos/1"));

        result.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(targetTodo)));
    }

    @Test
    void should_return_404_when_select_a_todo_but_todo_is_not_found() throws Exception {
        when(todoRepository.findById(1)).thenReturn(Optional.empty());

        ResultActions result = mockMvc.perform(get("/todos/1"));

        result.andExpect(status().isNotFound());
    }

    @Test
    void should_return_todo_when_save_a_todo() throws Exception {
        Todo todo = new Todo("title", true);

        ResultActions result = mockMvc.perform(
                post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todo))
        );

        result.andExpect(status().isCreated());
    }

    @Test
    void should_return_200_when_delete_a_todo_succeed() throws Exception {
        when(todoRepository.findById(1)).thenReturn(Optional.of(new Todo()));

        ResultActions result = mockMvc.perform(delete("/todos/1"));

        result.andExpect(status().isOk());
    }

    @Test
    void should_return_404_when_delete_a_todo_but_todo_not_exist() throws Exception {
        when(todoRepository.findById(1)).thenReturn(Optional.empty());

        ResultActions result = mockMvc.perform(delete("/todos/1"));

        result.andExpect(status().isNotFound());
    }

    @Test
    void should_return_200_and_updated_todo_when_update_a_todo_succeed() throws Exception {
        Todo existingTodo = new Todo("title", true);
        Todo newTodo = new Todo("updated title", true);
        String expectedResponseContent = objectMapper.writeValueAsString(existingTodo.merge(newTodo));
        when(todoRepository.findById(1)).thenReturn(Optional.of(existingTodo));

        ResultActions result = mockMvc.perform(
                patch("/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTodo))
        );

        result.andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent));
    }

    @Test
    void should_return_400_when_update_a_todo_failed_given_a_null_as_new_todo() throws Exception {
        Todo existingTodo = new Todo("title", true);
        when(todoRepository.findById(1)).thenReturn(Optional.of(existingTodo));

        ResultActions result = mockMvc.perform(
                patch("/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("")
        );

        result.andExpect(status().isBadRequest());
    }

    @Test
    void should_return_404_when_update_a_todo_but_todo_does_not_exist() throws Exception {
        when(todoRepository.findById(1)).thenReturn(Optional.empty());

        ResultActions result = mockMvc.perform(
                patch("/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Todo()))
        );

        result.andExpect(status().isNotFound());
    }

    private List<Todo> buildTodos() {
        return singletonList(new Todo("title", true));
    }
}
