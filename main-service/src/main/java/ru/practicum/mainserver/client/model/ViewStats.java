package ru.practicum.mainserver.client.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewStats {
    private String app;

    private String uri;

    private int hits;
}
