# Ray tracer for Android

![alt text](Example.gif)

## Portable
This Ray tracer is compatible with Android and Linux.
If you have docker installed, you can try it with ease by using following command to get the docker image:
```bash
docker pull ptpuscas/mobile_rt
```

The to execute the container, just type:
```bash
xhost +; docker run -v /tmp/.X11-unix:/tmp/.X11-unix -v ${PWD}/../WavefrontOBJs:/WavefrontOBJs -e DISPLAY=$DISPLAY -it ptpuscas/mobile_rt
```
And you should see a tiny cornell box with 2 spheres :)

## Create docker image
```bash
docker build -t ptpuscas/mobile_rt -f docker_image/Dockerfile --no-cache=false --build-arg build_type=Release .
```

## TODO

### Ray tracing engine
- [x] Implement loading of textures
- [ ] Add support for textures for different materials
- [x] Separate Material from Primitive in order to save memory
- [x] Improve BVH
- [x] Improve Regular Grid
- [ ] Add ray packet intersections
- [ ] Add gpu ray tracing support for comparison
- [ ] Add more types of shapes
- [ ] Support more types of models besides .obj files
- [x] Move naive acceleration structure to a class different than Scene
- [ ] Implement KD-Tree
- [ ] Make acceleration structures compatible with the lights
- [ ] Investigate if samplers are properly working
- [x] Add exceptions
- [x] Check all allocations' exceptions
- [x] Add normals per vertice for triangles
- [x] Improve loading of textures
- [ ] Parallelize build of Regular Grid
- [ ] Parallelize build of BVH
- [x] Add support for Boost

### Ray tracing JNI layer
- [ ] Refactor DrawView translation unit
- [ ] Improve DrawView code readability
- [x] Remove race conditions

### Ray tracing shaders
- [ ] Fix Path Tracing algorithm
- [ ] Improve shaders performance
- [ ] Add Bidirectional Path Tracing
- [ ] Add Metropolis light transport
- [ ] Add shader for debug purposes (wireframe of shapes and boxes)

### Ray tracing test cases
- [ ] Prepare more scene models with Blender for testing
- [x] Read and construct lights and cameras from files
- [x] Remove unnecessary primitives, lights and cameras in the code

### User Interface
- [x] Fix memory leak in Java UI
- [x] Fix load of obj files in Android 10
- [ ] Change Linux's UI from GTK to Qt
- [ ] Improve Java UI code to more Object Oriented
- [ ] Change Android icon

### System
- [x] Add comments in the Android UI
- [ ] Add comments in the JNI layer
- [ ] Add comments in the MobileRT
- [x] Give out of memory error when the memory is not enough to load the scene
- [ ] Add unit tests (more code coverage)
- [x] Add instrumented unit tests
- [x] Add git hooks to check git commit messages
- [x] Add git hooks to submit Jenkins' jobs after each git push
- [ ] Support to export rendered image to file
- [x] Add CI / CD support from github (actions) for the Google Test unit tests
- [ ] Remove repeated code
- [ ] Remove unnecessary casts

### Docker
- [x] Make a docker image with MobileRT
- [ ] Add models to the docker container
- [ ] Use docker compose to launch multiple containers and distribute the load

### Documentation
- [x] Improve README
- [ ] Write documentation
- [x] Update gif image
- [ ] Benchmark against popular ray tracers like PBRT
- [ ] Benchmark against previous version of MobileRT
