(ns future-app.ios.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [future-app.events]
            [future-app.subs]))

;; Fix 'Go Back' navigation
(js/require "react-native-gesture-handler")

(def ReactNative (js/require "react-native"))
(def ReactNavigation (js/require "react-navigation"))
(def create-stack-navigator (.-createStackNavigator ReactNavigation))
(def create-app-container (.-createAppContainer ReactNavigation))

(def app-registry (.-AppRegistry ReactNative))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def image (r/adapt-react-class (.-Image ReactNative)))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))

(def logo-img (js/require "./images/cljs.png"))

(defn alert [title]
      (.alert (.-Alert ReactNative) title))

(defn _firstScreen [props]
  (fn [props]
    [view {:style {:flex-direction "column" :margin 40 :align-items "center"}}
     [text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}} "@greeting"]
     [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5}
                           :on-press (fn []
                                        ;; which is equal to props.navigation.navigate("SecondScreen")
                                       (.navigate (.-navigation (clj->js props)) "SecondScreen"))}
      [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "next screen"]]]))

(defn _secondScreen [props]
  (fn [props]
    [view {:style {:flex-direction "column" :margin 40 :align-items "center"}}
      [text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}} "@greeting"]
      [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5}
                            :on-press (fn []
                                        (.goBack (.-navigation (clj->js props))))}
      [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "go back"]]]))

(def AppNavigator
  (create-stack-navigator
   (clj->js {:FirstScreen  (r/reactify-component _firstScreen)
             :SecondScreen (r/reactify-component _secondScreen)})
   (clj->js {:initialRouteName "FirstScreen"})))

(defn app-root [] [:> (create-app-container AppNavigator) {}])

(defn init []
      (dispatch-sync [:initialize-db])
      (.registerComponent app-registry "FutureApp" #(r/reactify-component app-root)))
