package pt.isel.leic.cn.server.repository;

import image_processor.Language;

import java.util.Arrays;
import java.util.List;

public class LanguageRepository {

    public static List<Language> getLanguages() {
        return Arrays.asList(
                Language.newBuilder().setName("Portuguese").setCode("pt").build(),
                Language.newBuilder().setName("English").setCode("en").build(),
                Language.newBuilder().setName("Spanish").setCode("es").build(),
                Language.newBuilder().setName("French").setCode("fr").build(),
                Language.newBuilder().setName("German").setCode("de").build(),
                Language.newBuilder().setName("Italian").setCode("it").build(),
                Language.newBuilder().setName("Dutch").setCode("nl").build(),
                Language.newBuilder().setName("Russian").setCode("ru").build(),
                Language.newBuilder().setName("Japanese").setCode("ja").build(),
                Language.newBuilder().setName("Chinese").setCode("zh").build(),
                Language.newBuilder().setName("Korean").setCode("ko").build()
                );
    }
}
