-- =============================================
-- Add Foreign Key Relations in Supabase
-- Run this in Supabase SQL Editor
-- =============================================

-- tour_tag.tour_id → tour.id
ALTER TABLE tour_tag
  ADD CONSTRAINT fk_tour_tag_tour
  FOREIGN KEY (tour_id) REFERENCES tour(id)
  ON DELETE CASCADE;

-- tour_item.tour_id → tour.id
ALTER TABLE tour_item
  ADD CONSTRAINT fk_tour_item_tour
  FOREIGN KEY (tour_id) REFERENCES tour(id)
  ON DELETE CASCADE;

-- tour_service.tour_id → tour.id
ALTER TABLE tour_service
  ADD CONSTRAINT fk_tour_service_tour
  FOREIGN KEY (tour_id) REFERENCES tour(id)
  ON DELETE CASCADE;

-- tour_visit.tour_id → tour.id
ALTER TABLE tour_visit
  ADD CONSTRAINT fk_tour_visit_tour
  FOREIGN KEY (tour_id) REFERENCES tour(id)
  ON DELETE CASCADE;

-- tour_event.tour_id → tour.id
ALTER TABLE tour_event
  ADD CONSTRAINT fk_tour_event_tour
  FOREIGN KEY (tour_id) REFERENCES tour(id)
  ON DELETE CASCADE;
