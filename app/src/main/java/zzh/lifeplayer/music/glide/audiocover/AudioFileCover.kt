/*
 * Copyright (c) 2019 Hemanth Savarala.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by
 *  the Free Software Foundation either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */
package zzh.lifeplayer.music.glide.audiocover

/** @author Karim Abou Zeid (kabouzeid)
 */
class AudioFileCover(val filePath: String) {

    init {
        require(filePath.isNotBlank()) { "File path cannot be blank. Received: '$filePath'" }
    }

    override fun hashCode(): Int {
        return filePath.hashCode()
    }

    override fun equals(other: Any?): Boolean {
/*        return if (other is AudioFileCover) {
            other.filePath == filePath
        } else false  */
        if (this === other) return true
        if (other !is AudioFileCover) return false
        return filePath == other.filePath
    }

    override fun toString(): String {
        return "AudioFileCover(filePath='$filePath')"
        
    }
}