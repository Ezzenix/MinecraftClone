package com.ezzenix.engine.opengl;

import com.ezzenix.engine.opengl.utils.FileUtil;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL33;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private int programId;

    public Shader(String vertexShaderPath, String fragmentShaderPath) {
        int vertexShader = loadShader(vertexShaderPath, GL_VERTEX_SHADER);
        int fragmentShader = loadShader(fragmentShaderPath, GL_FRAGMENT_SHADER);
        if (vertexShader == -1 || fragmentShader == -1) return;

        programId = glCreateProgram();
        glAttachShader(programId, vertexShader);
        glAttachShader(programId, fragmentShader);
        glLinkProgram(programId);

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == GL_FALSE) {
            int infoLogSize = GL20.glGetProgrami(programId, GL20.GL_INFO_LOG_LENGTH);
            System.err.println(GL20.glGetProgramInfoLog(programId, infoLogSize));
            System.err.println("Failed to link shader program!");
            return;
        }
        GL20.glValidateProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_VALIDATE_STATUS) == GL_FALSE) {
            int infoLogSize = GL20.glGetProgrami(programId, GL20.GL_INFO_LOG_LENGTH);
            System.err.println(GL20.glGetProgramInfoLog(programId, infoLogSize));
            System.err.println("Failed to validate shader program!");
        }
    }

    private static int loadShader(String path, int type) {
        String shaderSource = FileUtil.readResourceSource("shaders/" + path);
        if (shaderSource == null) return -1;
        int shader = glCreateShader(type);
        GL33.glShaderSource(shader, shaderSource);
        GL33.glCompileShader(shader);
        if (GL33.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Failed to compile shader at " + path + ": " + GL33.glGetShaderInfoLog(shader));
        }
        return shader;
    }

    public void use() {
        glUseProgram(programId);
    }

    public void uploadMat4f(String varName, Matrix4f mat4) {
        int varLocation = glGetUniformLocation(programId, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void uploadMat3f(String varName, Matrix3f mat3) {
        int varLocation = glGetUniformLocation(programId, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3.get(matBuffer);
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }

    public void uploadVec4f(String varName, Vector4f vec) {
        int varLocation = glGetUniformLocation(programId, varName);
        use();
        glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
    }

    public void uploadVec3f(String varName, Vector3f vec) {
        int varLocation = glGetUniformLocation(programId, varName);
        use();
        glUniform3f(varLocation, vec.x, vec.y, vec.z);
    }

    public void uploadVec2f(String varName, Vector2f vec) {
        int varLocation = glGetUniformLocation(programId, varName);
        use();
        glUniform2f(varLocation, vec.x, vec.y);
    }

    public void uploadFloat(String varName, float val) {
        int varLocation = glGetUniformLocation(programId, varName);
        use();
        glUniform1f(varLocation, val);
    }

    public void uploadInt(String varName, int val) {
        int varLocation = glGetUniformLocation(programId, varName);
        use();
        glUniform1i(varLocation, val);
    }

    public void uploadTexture(String varName, int slot) {
        int varLocation = glGetUniformLocation(programId, varName);
        use();
        glUniform1i(varLocation, slot);
    }

    public void uploadIntArray(String varName, int[] array) {
        int varLocation = glGetUniformLocation(programId, varName);
        use();
        glUniform1iv(varLocation, array);
    }
}