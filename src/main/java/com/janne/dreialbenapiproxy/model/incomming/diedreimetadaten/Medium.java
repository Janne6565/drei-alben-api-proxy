package com.janne.dreialbenapiproxy.model.incomming.diedreimetadaten;

import java.util.List;

public record Medium(
    List<Track> tracks,
    String ripLog
) {
}
