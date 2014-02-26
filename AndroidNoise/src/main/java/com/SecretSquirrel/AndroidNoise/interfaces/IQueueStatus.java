package com.SecretSquirrel.AndroidNoise.interfaces;

// Secret Squirrel Software - Created by bswanson on 2/26/14.

import com.SecretSquirrel.AndroidNoise.dto.PlayQueueTrack;

import java.util.ArrayList;

public interface IQueueStatus {
	ArrayList<PlayQueueTrack>   getPlayQueueItems();
}
