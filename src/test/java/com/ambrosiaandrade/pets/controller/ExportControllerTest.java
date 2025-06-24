package com.ambrosiaandrade.pets.controller;

import com.ambrosiaandrade.pets.service.ExportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExportController.class)
class ExportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExportService service;

    @Test
    void csv() throws Exception {
        byte[] bytes = "id,name\n1,dog".getBytes();
        when(service.getCsv()).thenReturn(bytes);

        mockMvc.perform(MockMvcRequestBuilders.get("/export/csv"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=pets.csv"))
                .andExpect(header().string("Content-Type", "text/csv; charset=UTF-8"))
                .andExpect(content().bytes(bytes));
    }

    @Test
    void pdf() throws Exception {
        byte[] bytes = new byte[]{1,2,3};
        when(service.getPdf()).thenReturn(bytes);

        mockMvc.perform(MockMvcRequestBuilders.get("/export/pdf"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=pets.pdf"))
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(content().bytes(bytes));
    }

}