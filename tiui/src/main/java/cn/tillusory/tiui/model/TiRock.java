package cn.tillusory.tiui.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import cn.tillusory.sdk.bean.TiRockEnum;
import cn.tillusory.tiui.R;

/**
 * Created by Anko on 2018/11/28.
 * Copyright (c) 2018-2020 拓幻科技 - tillusory.cn. All rights reserved.
 */
public enum TiRock {
    NO_FILTER(TiRockEnum.NO_ROCK, R.drawable.ic_ti_rock_0, R.string.none),
    DAZZLED_COLOR_ROCK(TiRockEnum.DAZZLED_COLOR_ROCK, R.drawable.ic_ti_rock_1, R.string.rock_dazzled_color),
    LIGHT_COLOR_ROCK(TiRockEnum.LIGHT_COLOR_ROCK, R.drawable.ic_ti_rock_2, R.string.rock_light_color),
    DIZZY_GIDDY_ROCK(TiRockEnum.DIZZY_GIDDY_ROCK, R.drawable.ic_ti_rock_3, R.string.rock_dizzy_giddy),
    ASTRAL_PROJECTION_ROCK(TiRockEnum.ASTRAL_PROJECTION_ROCK, R.drawable.ic_ti_rock_4, R.string.rock_astral_projection),
    BLACK_MAGIC_ROCK(TiRockEnum.BLACK_MAGIC_ROCK, R.drawable.ic_ti_rock_5, R.string.rock_black_magic),
    VIRTUAL_MIRROR_ROCK(TiRockEnum.VIRTUAL_MIRROR_ROCK, R.drawable.ic_ti_rock_6, R.string.rock_virtual_mirror),
    DYNAMIC_SPLIT_SCREEN_ROCK(TiRockEnum.DYNAMIC_SPLIT_SCREEN_ROCK, R.drawable.ic_ti_rock_7, R.string.rock_dynamic_split),
    BLACK_WHITE_FILM_ROCK(TiRockEnum.BLACK_WHITE_FILM_ROCK, R.drawable.ic_ti_rock_8, R.string.rock_black_white_film),
    GRAY_PETRIFACTION_ROCK(TiRockEnum.GRAY_PETRIFACTION_ROCK, R.drawable.ic_ti_rock_9, R.string.rock_gray_petrifaction),
    BULGE_DISTORTION_ROCK(TiRockEnum.BULGE_DISTORTION_ROCK, R.drawable.ic_ti_rock_10, R.string.rock_bulge_distortion);

    private final TiRockEnum rockEnum;
    private final int imageId;
    private final int stringId;

    TiRock(final TiRockEnum rockEnum, final @DrawableRes int imageId, final int stringId) {
        this.rockEnum = rockEnum;
        this.imageId = imageId;
        this.stringId = stringId;
    }

    public TiRockEnum getRockEnum() {
        return rockEnum;
    }

    public String getString(@NonNull Context context) {
        return context.getResources().getString(this.stringId);
    }

    public Drawable getImageDrawable(@NonNull Context context) {
        return context.getResources().getDrawable(imageId);
    }
}
