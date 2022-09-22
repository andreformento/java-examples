package com.formento.java19demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AppTest {

    @Mock
    private App app;

    @Test
    public void shouldAnswerWithTrue() {
        when(app.getNumber()).thenReturn(50);
        assertThat(app.getNumber()).isEqualTo(50);
    }

    @Test
    public void isInt() {
        String result = formatterPatternSwitch(1);
        assertThat(result).isEqualTo("int 1");
    }

    @Test
    public void isLong() {
        String result = formatterPatternSwitch(2L);
        assertThat(result).isEqualTo("long 2");
    }

    private String formatterPatternSwitch(Object o) {
        return switch (o) {
            case Integer i -> String.format("int %d", i);
            case Long l -> String.format("long %d", l);
            case Double d -> String.format("double %f", d);
            case String s -> String.format("String %s", s);
            default -> o.toString();
        };
    }

}
