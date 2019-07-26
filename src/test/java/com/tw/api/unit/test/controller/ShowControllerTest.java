package com.tw.api.unit.test.controller;

import com.tw.api.unit.test.services.ShowService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ShowController.class)
@ActiveProfiles(profiles = "test")
class ShowControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ShowService showService;

    @Test
    void getAll() throws Exception {
        //given
        when(showService.getShowLabel()).thenReturn("a beauty tag");
        //when
        ResultActions result = mvc.perform(get("/shows"));
        //then
        result.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", is("a beauty tag")));
    }
}