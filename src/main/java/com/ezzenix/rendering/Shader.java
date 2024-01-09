package com.ezzenix.rendering;

import com.ezzenix.utils.FileUtil;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL33;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class Shader {
    public static int load(String path, int type) {
        String shaderSource = FileUtil.readResourceSource("shaders/"+path);
        if (shaderSource == null) return -1;
        int shader = glCreateShader(type);
        GL33.glShaderSource(shader, shaderSource);
        GL33.glCompileShader(shader);
        if (GL33.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Failed to compile shader at " + path + ": " + GL33.glGetShaderInfoLog(shader));
        }
        return shader;
    }

    public static int makeProgram(String vertexShaderPath, String fragmentShaderPath) {
        int vertexShader = Shader.load(vertexShaderPath, GL_VERTEX_SHADER);
        int fragmentShader = Shader.load(fragmentShaderPath, GL_FRAGMENT_SHADER);
        if (vertexShader == -1 || fragmentShader == -1) return -1;

        int shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        if(GL20.glGetProgrami(shaderProgram, GL20.GL_LINK_STATUS) == GL_FALSE)
        {
            int infoLogSize = GL20.glGetProgrami(shaderProgram, GL20.GL_INFO_LOG_LENGTH);
            System.err.println(GL20.glGetProgramInfoLog(shaderProgram, infoLogSize));
            System.err.println("Failed to link shader program!");
            return -1;
        }
        GL20.glValidateProgram(shaderProgram);
        if (GL20.glGetProgrami(shaderProgram, GL20.GL_VALIDATE_STATUS) == GL_FALSE)
        {
            int infoLogSize=GL20.glGetProgrami(shaderProgram,GL20.GL_INFO_LOG_LENGTH);
            System.err.println(GL20.glGetProgramInfoLog(shaderProgram,infoLogSize));
            System.err.println("Failed to validate shader program!");
            return -1;
        }

        return shaderProgram;
    }
}