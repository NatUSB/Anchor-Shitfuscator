package wtf.krypton.util.shader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

/**
 * @author Demo
 */
public class ShaderUtil {

    /**
     * Loads and compiles the specified shader
     * @param source The shader source
     * @param type The type of shader
     * @return The program ID
     */
    public static int loadShader(String source, int type) {
        // Create the shader and assign the ID.
        int shaderID = glCreateShader(type);

        // Attach the shader source code to the shader program
        glShaderSource(shaderID, source);

        // Compile the shader
        glCompileShader(shaderID);

        // Check if the shader compilation was not successful
        if(glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println(glGetShaderInfoLog(shaderID, 500));
            throw new IllegalStateException("Could not compile shader!");
        }

        return shaderID;
    }

    /**
     * Creates and returns a new ByteBuffer
     * @param array The array of bytes
     * @return The new ByteBuffer
     */
    public static ByteBuffer createByteBuffer(byte[] array) {
        ByteBuffer result = ByteBuffer.allocateDirect(array.length).order(ByteOrder.nativeOrder());
        result.put(array).flip();
        return result;
    }

    /**
     * Creates and returns a new FloatBuffer
     * @param array The array of floats
     * @return The new FloatBuffer
     */
    public static FloatBuffer createFloatBuffer(float[] array) {
        FloatBuffer result = ByteBuffer.allocateDirect(array.length << 2).order(ByteOrder.nativeOrder()).asFloatBuffer();
        result.put(array).flip();
        return result;
    }

    /**
     * Creates and returns a new IntBuffer
     * @param array The array of ints
     * @return The new IntBuffer
     */
    public static IntBuffer createIntBuffer(int[] array) {
        IntBuffer result = ByteBuffer.allocateDirect(array.length << 2).order(ByteOrder.nativeOrder()).asIntBuffer();
        result.put(array).flip();
        return result;
    }

}

