//
// Created by Nicolas Degen on 2019-06-20.
//

#include "na-engine.h"

#include <fstream>
#include <iostream>
#include <streambuf>
#include <string>

#include <GLES2/gl2.h>

void NAEngine::draw() {
    static GLfloat green = 0;
    green += 0.01;
    if (green > 1) green = 0;
    glClear(GL_COLOR_BUFFER_BIT);
    glClearColor(0.5, green, 0.2, 0.9);
    return;
}

int NAEngine::compute(int value) {
    int a = value * foo_.value_or(0);
    foo_ = value;
    return a;
}

std::string NAEngine::readFile(const std::string& filepath) {
    std::ifstream t(filepath);
    std::string str((std::istreambuf_iterator<char>(t)),
                    std::istreambuf_iterator<char>());
    return str;
}
