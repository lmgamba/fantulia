package com.fantulia.backend.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

// @WebMvcTest picks up Filter beans, and MockMvc runs requests through them,
// so this exercises the real filter without a server or a database.
@WebMvcTest
class ApiNoStoreFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void successfulApiResponsesCarryNoStore() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, "no-store"));
    }

    @Test
    void errorResponsesUnderApiCarryNoStoreToo() throws Exception {
        mockMvc.perform(get("/api/does-not-exist"))
                .andExpect(status().isNotFound())
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, "no-store"));
    }
}
