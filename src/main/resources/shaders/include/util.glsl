vec3 positionFromMatrix(mat4 matrix) {
    return vec3(matrix[3][0], matrix[3][1], matrix[3][2]);
}
