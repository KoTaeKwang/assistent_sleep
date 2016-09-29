/*
 * status.c
 *
 *  Created on: Jul 31, 2016
 *      Author: KimNam
 */

#include "main.h"

static void
_status_hide_cb(void *data, Evas_Object *obj, void *event_info)
{
	if(!obj) return;
	elm_popup_dismiss(obj);
}

static void
_status_hide_finished_cb(void *data, Evas_Object *obj, void *event_info)
{
	if(!obj) return;
	evas_object_del(obj);
}

void status_cb(void *data, Evas_Object *obj, void *event_info)
{
	/*
	appdata_s *ad;

	ad = (appdata_s *) data;
	ad->conform = elm_conformant_add(ad->win);
	elm_win_indicator_mode_set(ad->win, ELM_WIN_INDICATOR_SHOW);
	elm_win_indicator_opacity_set(ad->win, ELM_WIN_INDICATOR_OPAQUE);
	evas_object_size_hint_weight_set(ad->conform, EVAS_HINT_EXPAND, EVAS_HINT_EXPAND);
	elm_win_resize_object_add(ad->win, ad->conform);
	evas_object_show(ad->conform);
	ad->HRMLabel = elm_label_add(ad->conform);
	ad->peekLabel = elm_label_add(ad->conform);
	ad->accerlerateLabel = elm_label_add(ad->conform);
	evas_object_move(ad->HRMLabel,20,20);
	evas_object_resize(ad->HRMLabel,300,30);
	evas_object_move(ad->peekLabel, 20, 50);
	evas_object_resize(ad->peekLabel, 300, 30);
	evas_object_move(ad->accerlerateLabel, 20, 80);
	evas_object_resize(ad->accerlerateLabel, 300, 30);
	evas_object_show(ad->HRMLabel);
	evas_object_show(ad->peekLabel);
	evas_object_show(ad->accerlerateLabel);

	evas_object_show(ad->win);
	 */

	Evas_Object *popup;
	Evas_Object *layout;
	Evas_Object *progressBar;
	appdata_s *ad;

	ad = (appdata_s *) data;

	popup = elm_popup_add(ad->win);
	elm_object_style_set(popup, "circle");
	evas_object_size_hint_weight_set(popup, EVAS_HINT_EXPAND, EVAS_HINT_EXPAND);
	eext_object_event_callback_add(popup, EEXT_CALLBACK_BACK, _status_hide_cb, NULL);
	evas_object_smart_callback_add(popup, "dismissed", _status_hide_finished_cb, NULL);

	layout = elm_layout_add(popup);
	elm_layout_theme_set(layout, "layout", "popup", "content/circle");

	elm_object_part_text_set(layout, "elm.text.title", "Current Status");

	progressBar = elm_progressbar_add(layout);
	elm_object_style_set(progressBar, "process/popup/small");
	evas_object_size_hint_align_set(progressBar, EVAS_HINT_FILL, 0.5);
	evas_object_size_hint_weight_set(progressBar, EVAS_HINT_EXPAND, EVAS_HINT_EXPAND);
	elm_progressbar_pulse(progressBar, EINA_TRUE);
	elm_object_part_content_set(layout, "elm.swallow.content", progressBar);
	evas_object_move(progressBar, 10, 50);
	evas_object_show(progressBar);

	ad->HRMLabel = elm_label_add(layout);
	elm_object_content_set(popup, layout);
	evas_object_move(ad->HRMLabel,10,230);
	evas_object_resize(ad->HRMLabel,340,30);
	evas_object_show(ad->HRMLabel);

	ad->check50Label = elm_label_add(layout);
	elm_object_content_set(popup, layout);
	evas_object_move(ad->check50Label,10,260);
	evas_object_resize(ad->check50Label,340,30);
	evas_object_show(ad->check50Label);
/*
	ad->accerlerateLabel = elm_label_add(layout);
	elm_object_content_set(popup, layout);
	evas_object_move(ad->accerlerateLabel,10,290);
	evas_object_resize(ad->accerlerateLabel,340,30);
	evas_object_show(ad->accerlerateLabel);
*/
	ad->check75Label = elm_label_add(layout);
	elm_object_content_set(popup, layout);
	evas_object_move(ad->check75Label,10,290);
	evas_object_resize(ad->check75Label,340,30);
	evas_object_show(ad->check75Label);

	evas_object_show(popup);
}
