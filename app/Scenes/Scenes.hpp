#ifndef APP_SCENES_HPP
#define APP_SCENES_HPP

#include "Components/Cameras/Orthographic.hpp"
#include "Components/Cameras/Perspective.hpp"
#include "MobileRT/Scene.hpp"

::MobileRT::Scene cornellBox_Scene(::MobileRT::Scene scene);

::std::unique_ptr<::MobileRT::Camera> cornellBox_Cam(float ratio);

::MobileRT::Scene cornellBox2_Scene(::MobileRT::Scene scene);

::std::unique_ptr<::MobileRT::Camera> cornellBox2_Cam(float ratio);

::MobileRT::Scene spheres_Scene(::MobileRT::Scene scene);

::std::unique_ptr<::MobileRT::Camera> spheres_Cam(float ratio);

::MobileRT::Scene spheres2_Scene(::MobileRT::Scene scene);

::std::unique_ptr<::MobileRT::Camera> spheres2_Cam(float ratio);

#endif //APP_SCENES_HPP
