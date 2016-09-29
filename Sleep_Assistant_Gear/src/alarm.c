/*
 * alarm.c
 *
 *  Created on: Jul 31, 2016
 *      Author: KimNam
 */

#include "main.h"

static void
_alarm_hide_cb(void *data, Evas_Object *obj, void *event_info)
{
	if(!obj) return;
	elm_popup_dismiss(obj);
}

static void
_alarm_hide_finished_cb(void *data, Evas_Object *obj, void *event_info)
{
	if(!obj) return;
	evas_object_del(obj);
}

void alarm_cb(void *data, Evas_Object *obj, void *event_info)
{
	Evas_Object *popup;
	Evas_Object *layout;
	appdata_s *ad;

	ad = (appdata_s *) data;

	popup = elm_popup_add(ad->win);
	elm_object_style_set(popup, "circle");
	evas_object_size_hint_weight_set(popup, EVAS_HINT_EXPAND, EVAS_HINT_EXPAND);
	eext_object_event_callback_add(popup, EEXT_CALLBACK_BACK, _alarm_hide_cb, NULL);
	evas_object_smart_callback_add(popup, "dismissed", _alarm_hide_finished_cb, NULL);

	layout = elm_layout_add(popup);
	elm_layout_theme_set(layout, "layout", "popup", "content/circle");
	elm_object_part_text_set(layout, "elm.text.title", "Alarm Info");
	elm_object_part_text_set(layout, "elm.text", "Alarm does not set yet...");
	elm_object_content_set(popup, layout);

	evas_object_show(popup);
}
