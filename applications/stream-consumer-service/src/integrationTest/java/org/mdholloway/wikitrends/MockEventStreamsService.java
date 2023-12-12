package org.mdholloway.wikitrends;

import io.quarkus.test.Mock;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Mock
@RestClient
@ApplicationScoped
public class MockEventStreamsService implements EventStreamsService {

    @Override
    public Multi<String> streamRevisionCreates() {
        return Multi.createFrom().items(
                "{ \"database\": \"enwiki\", \"rev_id\": 1, \"page_id\": 1, \"page_namespace\": 0, \"page_title\": \"test\" }"
        );
    }

    @Override
    public Multi<String> streamTagsChanges() {
        return Multi.createFrom().items(
                "{ \"database\": \"enwiki\", \"rev_id\": 1, \"page_id\": 1, \"page_namespace\": 0, \"page_title\": \"test\", \"tags\": [\"mw-reverted\"], \"prior_state\": { \"tags\": [] }}"
        );
    }
}
